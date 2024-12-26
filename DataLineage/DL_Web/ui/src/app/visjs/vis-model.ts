/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import * as vis from "vis";

export class VisModel<TVisNode extends vis.Node, TVisEdge extends vis.Edge> implements vis.Data {
    constructor(public nodes: vis.DataSet<TVisNode>,
                public edges: vis.DataSet<TVisEdge>) {
    }
}

export class VisClusterNode<TVisNode> implements vis.Node {
    public readonly icon: VisNodeIcon

    constructor(public readonly id: string,
                public readonly label: string,
                public readonly nodes: TVisNode[],
                public readonly isHighlighted: boolean = false) {
        this.icon = {
            face: "FontAwesome",
            size: 80,
            code: "\uf00a",
            color: isHighlighted ? "#ffa807" : "#a4c4df"
        }
    }
}

export type VisNodeIcon = {
    face?: string,
    code?: string,
    size?: number,
    color?: string
}