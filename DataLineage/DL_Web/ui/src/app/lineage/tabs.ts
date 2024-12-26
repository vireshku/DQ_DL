/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {maybe, Maybe} from "tsmonad";

export enum Tab { Summary, Operation }

export module Tab {
    export function toFragment(tab: Tab): string {
        return "tab-" + tab
    }

    export function fromFragment(s: string): Maybe<Tab> {
        return maybe(/^tab-(\d+)$/.exec(s)).map(groups => +groups[1])
    }
}
