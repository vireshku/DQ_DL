/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable, throwError} from "rxjs";
import {catchError, flatMap} from "rxjs/operators";
import {MatDialog, MatDialogConfig} from "@angular/material";
import {RetryPopupDialogComponent} from "./retry-popup-dialog.component";

@Injectable()
export class XHRTimeoutInterceptor implements HttpInterceptor {

    constructor(private dialog: MatDialog) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const self = this
        return next
            .handle(req)
            .pipe(catchError((res: HttpErrorResponse) => {
                if (res.status == 598) {
                    const dialogConfig = new MatDialogConfig()
                    dialogConfig.disableClose = true
                    return self.dialog
                        .open(RetryPopupDialogComponent, dialogConfig)
                        .afterClosed()
                        .pipe(flatMap(result =>
                            result
                                ? next.handle(req.clone({setHeaders: {"X-SPLINE-TIMEOUT": "-1"}}))
                                : throwError(res)))
                } else {
                    return throwError(res)
                }
            }))
    }
}