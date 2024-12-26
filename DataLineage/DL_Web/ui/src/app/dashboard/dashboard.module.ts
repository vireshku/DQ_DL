/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

// CSS, Icons and other UI frameworks stuff
import "font-awesome/css/font-awesome.min.css";
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/css/bootstrap-theme.min.css";
import "@angular/material/prebuilt-themes/indigo-pink.css";
import {MaterialModule} from "../material-extension/material.module";

// Other imports
import {CommonModule} from "@angular/common";
import {NgModule} from "@angular/core";
import {ReactiveFormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {HttpClientModule} from "@angular/common/http";
import {DashboardComponent} from "./dashboard.component";
import {DatasetBrowserComponent} from "./dataset-browser/dataset-browser.component";
import {DatasetBrowserService} from "./dataset-browser/dataset-browser.service";
import {WelcomeComponent} from "./welcome/welcome.component";
import {MiscModule} from "../misc/misc.module";
import {ScrollEventModule} from "ngx-scroll-event";

@NgModule({
    imports: [
        CommonModule,
        ReactiveFormsModule,
        RouterModule,
        HttpClientModule,
        MaterialModule,
        MiscModule,
        ScrollEventModule
    ],
    declarations: [
        DashboardComponent,
        DatasetBrowserComponent,
        WelcomeComponent
    ],
    providers: [
        DatasetBrowserService
    ],
    exports: [
        DashboardComponent,
        WelcomeComponent
    ]
})
export class DashboardModule {
}