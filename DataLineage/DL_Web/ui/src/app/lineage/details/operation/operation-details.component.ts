/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from "@angular/core";
import {IAttribute, IOperation} from "../../../../generated-ts/lineage-model";
import {LineageStore} from "../../lineage.store";
import {OperationType, typeOfExpr, typeOfOperation} from "../../types";
import {IExpression} from "../../../../generated-ts/operation-model";
import {getIconForNodeType} from "./operation-icon.utils";

import * as _ from "lodash"

@Component({
    selector: "operation-details",
    templateUrl: 'operation-details.component.html',
    styleUrls: ['operation-details.component.less']
})
export class OperationDetailsComponent implements OnChanges {

    constructor(private lineageStore: LineageStore) {
    }

    @Input() operation: IOperation
    @Input() selectedAttrIDs: string[]

    @Output() attributeSelected = new EventEmitter<IAttribute>()

    private operationType: OperationType

    keys(object: { [key: string]: any }) {
        return Object.keys(object)
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.operationType = typeOfOperation(this.operation)
    }

    getOperationIcon() {
        return getIconForNodeType(this.operationType).name
    }

    //noinspection JSMethodCanBeStatic
    getExprType(expr: IExpression) {
        return typeOfExpr(expr)
    }

    getAttribute(id: string): IAttribute {
        return this.lineageStore.lineageAccessors.getAttribute(id)
    }

    getDatasetAttributes(dsId: string): IAttribute[] {
        return this.lineageStore.lineageAccessors.getDatasetAttributes(dsId)
    }

    getDroppedAttributesIfAny(): IAttribute[] | undefined {
        const attributeIdsByDatasetId =
            (dsId: string): string[] => this.lineageStore.lineageAccessors.getDataset(dsId).schema.attrs

        const inputAttributeIds: string[] = _.flatMap(this.operation.mainProps.inputs, attributeIdsByDatasetId)
        const outputAttributeIds: string[] = attributeIdsByDatasetId(this.operation.mainProps.output)

        const removedAttributesSortedByName =
            _(inputAttributeIds).difference(outputAttributeIds)
                .map(attrId => this.lineageStore.lineageAccessors.getAttribute(attrId))
                .sortBy(attr => attr.name)
                .value()

        return removedAttributesSortedByName.length
            ? removedAttributesSortedByName
            : undefined
    }

    selectAttribute(attrId: string) {
        this.attributeSelected.emit(this.lineageStore.lineageAccessors.getAttribute(attrId))
    }

}