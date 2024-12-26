/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";

import "primeng/resources/primeng.min.css";
import "primeng/resources/themes/omega/theme.css";
import {AccordionModule, SharedModule} from "primeng/primeng";

import {OperationIconComponent} from "./operation/operation-icon.component";
import {OperationDetailsComponent} from "./operation/operation-details.component";
import {DetailsSectionHeaderComponent} from "./details-section-header.component";
import {DetailsPanelHeaderComponent} from "./details-panel-header.component";
import {ExpressionInlineComponent} from "./expression/expression-inline.component";
import {ExpressionDialogComponent} from "./expression/expression-dialog.component";
import {TreeModule} from "angular-tree-component";
import {AttributeListComponent} from "./attribute-list/attribute-list.component";
import {DataTypeViewComponent} from "./attribute-list/data-type-view/data-type-view.component";

@NgModule({
    imports: [
        CommonModule,
        AccordionModule,
        SharedModule,
        TreeModule.forRoot()
    ],
    declarations: [
        OperationDetailsComponent,
        OperationIconComponent,
        DetailsPanelHeaderComponent,
        DetailsSectionHeaderComponent,
        ExpressionInlineComponent,
        ExpressionDialogComponent,
        AttributeListComponent,
        DataTypeViewComponent
    ],
    entryComponents: [
        ExpressionDialogComponent
    ],
    exports: [
        OperationIconComponent,
        OperationDetailsComponent,
        AttributeListComponent,
        DetailsPanelHeaderComponent
    ]
})
export class DetailsModule {

}