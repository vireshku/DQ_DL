/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import * as vis from "vis";
import {IOperation} from "../../../generated-ts/lineage-model";
import {typeOfOperation} from "../types";
import {VisNodeIcon} from "../../visjs/vis-model";
import {getIconForNodeType} from "../details/operation/operation-icon.utils";

export class VisNode implements vis.Node {
    public readonly id: string
    public readonly icon: VisNodeIcon

    constructor(public readonly operation: IOperation,
                public readonly label: string,
                public readonly isHighlighted: boolean = false) {
        this.id = operation.mainProps.id
        this.icon = getIconForNodeType(typeOfOperation(operation))
            .toVisNodeIcon(isHighlighted ? "#ff9600" : "#337ab7")
    }
}

export class VisEdge implements vis.Edge {
    from: string;
    to: string;
    title: string;

    constructor(from: string, to: string, title: string) {
        this.from = from;
        this.to = to;
        this.title = title;
    }
}
