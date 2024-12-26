/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component, Input} from "@angular/core";
import {IOperation} from "../../../../generated-ts/lineage-model";
import {typeOfOperation} from "../../types";
import {getIconForNodeType} from "./operation-icon.utils";

@Component({
    selector: "operation-icon",
    template: "<i class='fa {{faIconCode}}'></i>",
    styles: ["i { color: steelblue; }"]
})
export class OperationIconComponent {
    faIconCode: string

    @Input() set operation(op: IOperation) {
        this.faIconCode = op && getIconForNodeType(typeOfOperation(op)).name
    }
}