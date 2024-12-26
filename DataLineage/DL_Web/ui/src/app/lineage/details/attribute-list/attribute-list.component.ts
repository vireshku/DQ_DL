/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component, EventEmitter, Input, Output, ViewChild} from "@angular/core";
import {IAttribute} from "../../../../generated-ts/lineage-model";
import {IArray, IDataType, IStruct} from "../../../../generated-ts/datatype-model";
import {typeOfDataType} from "../../types";
import {IActionMapping, ITreeOptions, TreeComponent} from "angular-tree-component";
import {ITreeNode} from 'angular-tree-component/dist/defs/api';
import * as _ from 'lodash';
import {LineageStore} from "../../lineage.store";

@Component({
    selector: "attribute-list",
    templateUrl: "attribute-list.component.html",
    styleUrls: ["attribute-list.component.less"]
})
export class AttributeListComponent {

    @Input() set attrs(attrs: IAttribute[]) {
        this.attrTree = attrs.map(a => this.buildAttrTree(a))
    }

    @Input() set selectedAttrIDs(ids: string[]) {
        this.selectedIds = ids
        this.highlightSelected()
    }

    @Input() expandRoot: boolean = false

    @Output() attrClicked = new EventEmitter<string>()

    attrTree: INodeData[]

    @ViewChild('tree') treeComponent: TreeComponent;

    readonly actionMapping: IActionMapping = {
        mouse: {
            click: (tree, node, $event) => this.onNodeClicked(node)
        }
    }

    readonly treeOptions: ITreeOptions = {
        actionMapping: this.actionMapping,
        allowDrag: false,
        allowDrop: false,
    }

    constructor(private lineageStore: LineageStore) {
    }

    onTreeInit() {
        this.highlightSelected()
    }

    onNodeClicked(node: ITreeNode) {
        this.attrClicked.emit(node.data.attributeId)
    }

    private selectedIds: string[]

    private buildAttrTree(attr: IAttribute): INodeData {
        const attributeId = attr.id
        const getDataType = _.bind(this.lineageStore.lineageAccessors.getDataType, this.lineageStore.lineageAccessors)

        function buildChildren(dt: IDataType): (INodeData[] | undefined) {
            let dtt = typeOfDataType(dt)
            return (dtt == "Simple") ? undefined
                : (dtt == "Struct") ? buildChildrenForStructType(<IStruct> dt)
                    : buildChildren(getDataType((<IArray> dt).elementDataTypeId))
        }

        function buildChildrenForStructType(sdt: IStruct): INodeData[] {
            return sdt.fields.map(f => buildNode(getDataType(f.dataTypeId), f.name, false))
        }

        function buildNode(dt: IDataType, name: string, isExpanded: boolean) {
            return {
                attributeId: attributeId,
                name: name,
                type: dt,
                children: buildChildren(dt),
                isExpanded: isExpanded
            }
        }

        return buildNode(getDataType(attr.dataTypeId), attr.name, this.expandRoot)
    }

    private highlightSelected(): void {
        if (this.treeComponent && this.treeComponent.treeModel.roots) {
            let treeModel = this.treeComponent.treeModel
            treeModel.roots.forEach((node: ITreeNode) => {
                node.setIsActive(this.isSelected(node, this.selectedIds), true)
            })
        }
    }

    private isSelected(node: ITreeNode, ids: string[]): boolean {
        let id = node.data.attributeId
        return id != null && _.includes(ids, id)
    }

}

interface INodeData {
    name: string,
    type: IDataType,
    attributeId: string,
    children: INodeData[],
    isExpanded: boolean
}
