/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component} from "@angular/core";

declare const __APP_VERSION__: string

@Component({
    selector: "version",
    template: `
        <span class="small text-muted">Spline v{{ appVersion }}</span>
    `,
    styles: [`span {
        position: absolute;
        right: 0;
        bottom: 0;
        margin: 2px 6px
    }`]
})
export class VersionComponent {
    appVersion: string = __APP_VERSION__
}