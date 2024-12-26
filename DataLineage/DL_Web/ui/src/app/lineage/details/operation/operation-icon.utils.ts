/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import {OperationType} from "../../types";
import {VisNodeIcon} from "../../../visjs/vis-model";

export class Icon {
    constructor(public name: string,
                public code: string,
                public font: string) {
    }

    public toVisNodeIcon(color: string): VisNodeIcon {
        return {
            face: this.font,
            size: 80,
            code: this.code,
            color: color
        }
    }
}

export function getIconForNodeType(nodeType: OperationType): Icon {
    let font = "FontAwesome";

    switch (nodeType) {
        case "Write":
            return new Icon("fa-floppy-o", "\uf0c7", font);

        case "Filter":
            return new Icon("fa-filter", "\uf0b0", font);

        case "Sort":
            return new Icon("fa-sort-amount-desc", "\uf161", font);

        case "Aggregate":
            return new Icon("fa-calculator", "\uf1ec", font);

        case "Join":
            return new Icon("fa-code-fork", "\uf126", font);

        case "Union":
            return new Icon("fa-bars", "\uf0c9", font);

        case "Projection":
            return new Icon("fa-chevron-circle-down", "\uf13a", font);

        case "Read":
            return new Icon("fa-database", "\uf1c0", font);

        case "Alias":
            return new Icon("fa-circle-thin", "\uf1db", font);

        case "Composite":
            return new Icon("fa-cogs", "\uf085", font);

        case "Generic":
            return new Icon("fa-question-circle", "\uf059", font);

        default:
            return null;
    }
}
