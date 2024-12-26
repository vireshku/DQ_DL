/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component, HostBinding, Input, OnChanges, SimpleChanges} from "@angular/core";

@Component({
    selector: "loading-indicator",
    templateUrl: "loading-indicator.component.html",
    styleUrls: ["loading-indicator.component.less"],
})
export class LoadingIndicatorComponent implements OnChanges {
    @Input("active")
    isActive: boolean = false

    @Input("delayed")
    @HostBinding('class.delayed')
    isDelayed: boolean = true

    @HostBinding('class.active')
    showActive: boolean = false

    ngOnChanges(changes: SimpleChanges): void {
        if (changes["isActive"]) setTimeout(() => this.showActive = this.isActive)
    }
}