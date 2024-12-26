/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {VersionComponent} from "./version.component";
import {LoadingIndicatorComponent} from "./loading-indicator/loading-indicator.component";

@NgModule({
    imports: [
        CommonModule
    ],
    declarations: [
        VersionComponent,
        LoadingIndicatorComponent
    ],
    exports: [
        VersionComponent,
        LoadingIndicatorComponent
    ]
})

export class MiscModule {
}