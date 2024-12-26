/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component, Input} from "@angular/core";
import {TreeNode} from "angular-tree-component";
import {typeOfDataType} from "../../../types";
import {IArray, IDataType} from "../../../../../generated-ts/datatype-model";
import {LineageStore} from "../../../lineage.store";

@Component({
    selector: "data-type-view",
    templateUrl: "data-type-view.component.html",
    styleUrls: ["data-type-view.component.less"]
})
export class DataTypeViewComponent {
    @Input() node: TreeNode

    constructor(private lineageStore: LineageStore) {
    }

    typeOfDataType(dt: IDataType): string {
        return typeOfDataType(dt)
    }

    getArrayElementType(arrayDT: IArray): IDataType {
        return this.lineageStore.lineageAccessors.getDataType(arrayDT.elementDataTypeId)
    }

    toggleExpanded(e: Event) {
        this.node.toggleExpanded()
        DataTypeViewComponent.suppressBrowserNativeBehaviour(e)
    }

    private static suppressBrowserNativeBehaviour(event:Event) {
        event.stopPropagation()
        let btn = <HTMLButtonElement> event.srcElement
        btn.blur()
    }
}