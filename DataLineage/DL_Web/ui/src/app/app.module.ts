/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { RouterModule, Routes, UrlSegment } from "@angular/router";
import "hammerjs/hammer";
import { AppComponent } from "./app.component";
import { DashboardModule } from "./dashboard/dashboard.module";
import { DashboardComponent } from "./dashboard/dashboard.component";
import { LineageComponent } from "./lineage/lineage.component";
import { LineageModule } from "./lineage/lineage.module";
import { LineageByDatasetIdResolver } from "./lineage/lineage.resolver";
import { WelcomeComponent } from "./dashboard/welcome/welcome.component";
import { PersistentDatasetResolver } from "./dataset/dataset.resolver";
import { DatasetModule } from "./dataset/dataset.module";
import { DatasetLineageOverviewResolver } from "./dataset/lineage-overview/lineage-overview.resolver";
import { DatasetLineageOverviewComponent } from "./dataset/lineage-overview/lineage-overview.component";
import { MiscModule } from "./misc/misc.module";
import { XHRTimeoutRectifierModule } from "./xhr-timeout-rectifier/xhr-timeout-rectifier.module";
import { LineageStore } from "./lineage/lineage.store";
import {VisModule} from "./visjs/vis.module";


const lineageRoute = {
    component: LineageComponent,
    matcher: (url: UrlSegment[]) =>
        (url.length === 0)
            ? { consumed: url }
            : (url.length === 2 && url[0].path === 'op')
                ? { consumed: url, posParams: { 'operationId': url[1] } }
                : null
}

const datasetRoute = {
    path: "dataset/:id",
    resolve: { dataset: PersistentDatasetResolver },
    children: [
        {
            path: "lineage",
            children: [
                {
                    path: "overview",
                    resolve: { lineage: DatasetLineageOverviewResolver },
                    component: DatasetLineageOverviewComponent
                },
                {
                    path: "partial",
                    resolve: { lineage: LineageByDatasetIdResolver },
                    children: [lineageRoute]
                }
            ]
        }
    ]
}

const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: '/dashboard'
    },
    {
        path: 'dashboard',
        component: DashboardComponent,
        children: [
            {
                path: '',
                pathMatch: 'full',
                component: WelcomeComponent
            },
            datasetRoute
        ]
    },
    datasetRoute,
    {
        path: '**',
        redirectTo: '/dashboard'
    },
]

@NgModule({
    imports: [
        BrowserModule,
        RouterModule.forRoot(routes, { enableTracing: false }),
        XHRTimeoutRectifierModule,
        DashboardModule,
        LineageModule,
        DatasetModule,
        VisModule,
        MiscModule
    ],
    providers: [
        LineageStore
    ],
    declarations: [AppComponent],
    bootstrap: [AppComponent]
})
export class AppModule {
}