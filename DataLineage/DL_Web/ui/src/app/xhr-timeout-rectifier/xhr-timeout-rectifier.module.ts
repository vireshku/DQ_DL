import {NgModule} from "@angular/core";
import {HTTP_INTERCEPTORS} from "@angular/common/http";
import {AppComponent} from "../app.component";
import {RetryPopupDialogComponent} from "./retry-popup-dialog.component";
import {XHRTimeoutInterceptor} from "./xhr-timeout-interceptor.service";
import {MatButtonModule, MatDialogModule, MatIconModule} from "@angular/material";

/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */



@NgModule({
    imports: [
        MatButtonModule,
        MatDialogModule
    ],
    providers: [
        {provide: HTTP_INTERCEPTORS, useClass: XHRTimeoutInterceptor, multi: true}
    ],
    declarations: [
        RetryPopupDialogComponent
    ],
    entryComponents: [
        RetryPopupDialogComponent
    ],
})

export class XHRTimeoutRectifierModule {
}