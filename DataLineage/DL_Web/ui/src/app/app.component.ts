/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component} from '@angular/core';
import {Event as RouterEvent, NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router} from "@angular/router";

@Component({
    selector: 'app',
    template: `
        <router-outlet></router-outlet>
        <loading-indicator [active]="loading"></loading-indicator>
    `,
})
export class AppComponent {
    loading: boolean = false

    constructor(private router: Router) {
        router.events.subscribe((event: RouterEvent) => {
            this.updateLoadingStatus(event)
        })
    }

    updateLoadingStatus(event: RouterEvent): void {
        if (event instanceof NavigationStart) {
            this.loading = true
        }
        if (event instanceof NavigationEnd
            || event instanceof NavigationCancel
            || event instanceof NavigationError) {
            this.loading = false
        }
    }
}