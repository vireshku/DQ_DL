/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {NgModule} from "@angular/core";
import {HttpClientModule} from "@angular/common/http";

import "@angular/material/prebuilt-themes/indigo-pink.css";

import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/css/bootstrap-theme.min.css";
import "font-awesome/css/font-awesome.min.css";
import {PersistentDatasetResolver} from "./dataset.resolver";
import {DatasetService} from "./dataset.service";
import {DatasetLineageOverviewResolver} from "./lineage-overview/lineage-overview.resolver";
import {DatasetLineageOverviewComponent} from "./lineage-overview/lineage-overview.component";
import {MiscModule} from "../misc/misc.module";
import {MaterialModule} from "../material-extension/material.module";
import {LineageOverviewGraphComponent} from "./lineage-overview/lienage-overview-graph.component";
import {CommonModule} from "@angular/common";
import {DetailsModule} from "../lineage/details/details.module";


@NgModule({
    imports: [
        HttpClientModule,
        MaterialModule,
        MiscModule,
        CommonModule,
        DetailsModule
    ],
    declarations: [
        DatasetLineageOverviewComponent,
        LineageOverviewGraphComponent
    ],
    providers: [
        DatasetService,
        PersistentDatasetResolver,
        DatasetLineageOverviewResolver
    ]
})
export class DatasetModule {

}