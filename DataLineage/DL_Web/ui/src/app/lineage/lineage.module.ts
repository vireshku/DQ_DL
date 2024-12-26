/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import { NgModule } from "@angular/core";
import { LineageComponent } from "./lineage.component";
import { LineageService } from "./lineage.service";
import { CommonModule } from "@angular/common";
import { HttpClientModule } from "@angular/common/http";
import { RouterModule } from "@angular/router";


import "@angular/material/prebuilt-themes/indigo-pink.css";
import { NoopAnimationsModule } from "@angular/platform-browser/animations";
import { MaterialModule } from "../material-extension/material.module";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/css/bootstrap-theme.min.css";
import "font-awesome/css/font-awesome.min.css";

import { GraphComponent } from "./graph/graph.component";
import { LineageByDatasetIdResolver } from "./lineage.resolver";
import { DetailsModule } from "./details/details.module";
import { MiscModule } from "../misc/misc.module";
import { ExpressionRenderService } from "./details/expression/expression-render.service";

@NgModule({
    imports: [
        NoopAnimationsModule,
        CommonModule,
        RouterModule,
        MaterialModule,
        HttpClientModule,
        DetailsModule,
        MiscModule
    ],
    declarations: [
        LineageComponent,
        GraphComponent
    ],
    providers: [
        LineageService,
        LineageByDatasetIdResolver,
        ExpressionRenderService
    ],
    exports: [
        LineageComponent,
        GraphComponent
    ]
})
export class LineageModule {

}