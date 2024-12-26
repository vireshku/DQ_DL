/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import * as vis from "vis";
import {IComposite, ITypedMetaDataSource} from "../../../generated-ts/operation-model";
import {VisNodeIcon} from "../../visjs/vis-model";

export interface GraphNode {
    type: GraphNodeType
    id: string
}

export enum VisNodeType {
    Process,
    Dataset
}

export abstract class VisNode implements vis.Node {
    protected constructor(public nodeType: VisNodeType,
                public id: string,
                public label: string,
                public icon: VisNodeIcon) {
    }
}

export class VisProcessNode extends VisNode {
    constructor(public operation: IComposite,
                id: string,
                label: string,
                icon: VisNodeIcon) {
        super(VisNodeType.Process, id, label, icon)
    }
}

export class VisDatasetNode extends VisNode {
    constructor(public dataSource: ITypedMetaDataSource,
                id: string,
                title: string,
                label: string,
                icon: VisNodeIcon) {
        super(VisNodeType.Dataset, id, label, icon)
    }
}

export const ID_PREFIX_LENGTH = 3

export const ID_PREFIXES = {
    operation: "op_",
    datasource: "ds_",
    extra: "ex_",
    datasource_cluster: "cl_",
}

export const GraphNodeTypesByIdPrefixes: { [key: string]: GraphNodeType } = {
    op_: "operation",
    ds_: "datasource",
    cl_: "datasource_cluster",
}

export type GraphNodeType = (
    "operation"
    | "datasource"
    | "datasource_cluster"
    )
