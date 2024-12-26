/*
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import {Component, OnInit} from "@angular/core";
import {FormControl} from '@angular/forms';
import {DatasetBrowserService} from "./dataset-browser.service";
import {IPersistedDatasetDescriptor} from "../../../generated-ts/lineage-model";
import {SearchRequest} from "./dataset-browser.model";
import {BehaviorSubject, identity, timer} from "rxjs";
import {ScrollEvent} from "ngx-scroll-event";
import {debounce, distinct, filter} from "rxjs/operators";

@Component({
    selector: "dataset-browser",
    templateUrl: "dataset-browser.component.html",
    styleUrls: ["dataset-browser.component.less"]
})
export class DatasetBrowserComponent implements OnInit {

    descriptors: IPersistedDatasetDescriptor[]

    searchText = new FormControl()

    private searchRequest$ = new BehaviorSubject<SearchRequest>(null)

    constructor(private dsBrowserService: DatasetBrowserService) {
    }

    ngOnInit(): void {
        this.searchText.valueChanges
            .pipe(debounce(v => timer(v ? 300 : 0)))
            .forEach(this.newSearch.bind(this))

        this.searchRequest$
            .pipe(
                distinct(),
                filter(<any>identity))
            .subscribe((sr:SearchRequest) =>
                this.dsBrowserService
                    .getLineageDescriptors(sr)
                    .then(descriptors => {
                        if (sr == this.searchRequest$.getValue()) {
                            if (sr.offset == 0 ){
                                this.descriptors = descriptors
                            } else {
                                this.descriptors.push(...descriptors)
                            }
                        }
                    }))

        // set initial values
        this.searchText.setValue("")
    }

    newSearch(text: string) {
        this.searchRequest$.next(new SearchRequest(text))
    }

    onScroll(e: ScrollEvent) {
        if (!e.isWindowEvent && e.isReachingBottom)
            this.searchRequest$.next(
                this.searchRequest$.getValue().withOffset(this.descriptors.length))
    }
}