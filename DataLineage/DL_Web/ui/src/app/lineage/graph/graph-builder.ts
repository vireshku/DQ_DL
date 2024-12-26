/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {IAttribute, IDataLineage, IMetaDataset, IOperation} from "../../../generated-ts/lineage-model";
import {VisNode, VisEdge} from "./graph.model";
import * as vis from "vis";
import * as _ from "lodash";
import {OperationType, typeOfOperation} from "../types";
import {VisModel} from "../../visjs/vis-model";
import {ExpressionRenderService} from "../details/expression/expression-render.service";
import {IJoin, IFilter, ISort, IRead, IWrite} from "../../../generated-ts/operation-model";

export function lineageToGraph(
    lineage: IDataLineage,
    expressionRenderService: ExpressionRenderService,
    selectedOperationId: string | undefined,
    hiddenOperationTypes: OperationType[] = []): VisModel<VisNode, VisEdge> {

    const operationVisibilityPredicate = (op: IOperation) => {
            let opType = typeOfOperation(op)
            return op.mainProps.id == selectedOperationId
                || opType != "Alias"
                && hiddenOperationTypes.indexOf(opType) < 0
        },
        operationsByVisibility = _.groupBy(lineage.operations, operationVisibilityPredicate),
        visibleOperations: IOperation[] = operationsByVisibility.true,
        hiddenOperations: IOperation[] = operationsByVisibility.false,
        hiddenOpIds: string[] = _.map(hiddenOperations, "mainProps.id"),
        visibleNodes = visibleOperations.map(op => {
            return new VisNode(op, getLabel(op, expressionRenderService))
        }),
        visibleEdges = createVisibleEdges(lineage, hiddenOpIds)

    return new VisModel(
        new vis.DataSet<VisNode>(visibleNodes),
        new vis.DataSet<VisEdge>(visibleEdges))
}

/**
 * Get label from an operation
 *
 * @param operation the operation where the label should be retrieved
 * @param expressionRenderService service to render expressions
 *
 * @returns the label from the operation
 */
export function getLabel(operation: IOperation, expressionRenderService: ExpressionRenderService) {
    let label = operation.mainProps.name + "\n"
    switch (typeOfOperation(operation)) {
        case "Aggregate":
            let aggregationExpressions = (<any>operation).aggregations
            label += Object.keys(aggregationExpressions).join(",")
            break
        case "Join":
            let joinExpression = (<IJoin>operation).condition
            label += expressionRenderService.getText(joinExpression)
            break
        case "Filter":
            let filterExpression = (<IFilter>operation).condition
            label += expressionRenderService.getText(filterExpression)
            break
        case "Projection":
            // TODO : Define what to display for the second label
            break
        case "Sort":
            let sortExpressions = (<ISort>operation).orders
            label += sortExpressions[0].direction + " " + expressionRenderService.getText(sortExpressions[0])
            break
        case "Read":
            let readExpression = (<IRead>operation).sources
            label += getFileName(readExpression[0].path)
            break
        case "Write":
            label += (<IWrite>operation).path
            break
        default:
            label = operation.mainProps.name
    }

    return _.truncate(label, {length: 50});
}

/**
 * Get the filename from a path
 *
 * @param path string where the filename should be extracted
 *
 * @returns the the filename of the path in parameters
 */
function getFileName(path: string): string {
    return path.replace(/^.*[\\\/]/, '')
}

function createVisibleEdges(lineage: IDataLineage, hiddenOpIds: string[]): VisEdge[] {
    let opIdsByInDsId: { [key: string]: string[] } = {},
        opIdByOutDsId: { [key: string]: string } = {}

    // group operations by their input & output
    lineage.operations.forEach(op => {
        let opId = op.mainProps.id
        opIdByOutDsId[op.mainProps.output] = opId
        op.mainProps.inputs.forEach(inDsId => {
            let opsForInput = opIdsByInDsId[inDsId] || []
            opIdsByInDsId[inDsId] = opsForInput
            opsForInput.push(opId)
        })
    })

    // create edges
    let attrsById: { [id: string]: IAttribute } = _.mapValues(_.groupBy(lineage.attributes, "id"), _.first),
        datasetsById: { [id: string]: IMetaDataset } = _.mapValues(_.groupBy(lineage.datasets, "id"), _.first),
        datasetIdsThatFormEdges = _.intersection(_.keys(opIdsByInDsId), _.keys(opIdByOutDsId)),
        edges: { from: string, to: string, dsId: string }[] = _.flatMap(datasetIdsThatFormEdges, dsId => {
            let fromOperationId = opIdByOutDsId[dsId]
            return opIdsByInDsId[dsId]
                .filter(toOperationId => fromOperationId != toOperationId)
                .map(toOperationId => ({
                    from: fromOperationId,
                    to: toOperationId,
                    dsId: dsId
                }))
        })

    // reduce edges by visibility
    hiddenOpIds.forEach(hiddenOpId => {
        let hiddenInputEdges = _.remove(edges, e => e.to == hiddenOpId)
        if (hiddenInputEdges.length != 1)
            throw Error("Unexpected number of input edges (" + hiddenInputEdges.length + ") for operation (" + hiddenOpId + ") that should be unary")
        let parentOpId = hiddenInputEdges[0].from
        edges.filter(e => e.from == hiddenOpId).forEach(e => e.from = parentOpId)
    })

    // convert to vis.Edge
    return edges.map(e => {
        let attrNames = datasetsById[e.dsId].schema.attrs.map(attrId => attrsById[attrId].name),
            edgeTitle = '[' + attrNames.join(', ') + ']'
        return new VisEdge(e.from, e.to, edgeTitle)
    })
}
