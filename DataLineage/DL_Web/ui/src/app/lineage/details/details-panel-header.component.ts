/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component, Input} from "@angular/core";

@Component({
    selector: 'details-panel-header',
    template: `
        <i class="fa fa-2x"
           [ngClass]="faIcon"
           [ngStyle]="{color: iconColor}"></i>
        <b>{{caption}}</b>
    `,
    styles: [`
        :host {
            display: block;
            height: 48px;
        }
        i {
            position: absolute; 
            top: 11px; 
            left: 12px; 
            text-shadow: 1px 1px white;
        }
        b {
            text-align: center; 
            display: block; 
            padding-top: 15px;
        }
    `]
})
export class DetailsPanelHeaderComponent {
    @Input() caption: string
    @Input() faIcon: string
    @Input() iconColor: string
}