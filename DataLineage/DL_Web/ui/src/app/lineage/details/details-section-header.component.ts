/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import {Component, Input, EventEmitter, Output} from "@angular/core";
import {IAttribute} from "../../../generated-ts/lineage-model";
import * as _ from "lodash"

@Component({
    selector: 'details-section-header',
    template: `
        <div>
            <i class="fa" [ngClass]="faIcon" [ngStyle]="{color: iconColor}"></i>
            {{caption}}
        </div>
    `,
    styles: [`
        :host {
            display: inline-block;
        }
        i {
            padding: 0 2px 0 5px;            
        }
    `]
})
export class DetailsSectionHeaderComponent {
    @Input() caption: string
    @Input() faIcon: string
    @Input() iconColor: string
}