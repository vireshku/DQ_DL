/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component, OnDestroy, OnInit} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {IAttribute, IDataLineage, IOperation} from "../../generated-ts/lineage-model";
import {LineageStore} from "./lineage.store";
import {OperationType, typeOfOperation} from "./types";
import * as _ from "lodash";
import {MatTabChangeEvent} from "@angular/material";
import {Tab} from "./tabs";
import {Subscription} from "rxjs";

@Component({
    templateUrl: 'lineage.component.html',
    styleUrls: ['lineage.component.less'],
    providers: []
})
export class LineageComponent implements OnInit, OnDestroy {
    lineage: IDataLineage
    selectedTabIndex: Tab = Tab.Summary
    selectedOperation?: IOperation
    selectedAttrIDs: string[]
    highlightedNodeIDs: string[]

    hideableOperationTypes: OperationType[] = ['Projection', 'Filter', 'Sort', 'Aggregate']
    presentHideableOperationTypes: OperationType[]
    hiddenOperationTypes: OperationType[]

    private subscriptions: Subscription[] = []

    isOperationTypeVisible(opType: OperationType) {
        return this.hiddenOperationTypes.indexOf(opType) < 0
    }

    constructor(private router: Router,
                private route: ActivatedRoute,
                private lineageStore: LineageStore) {
    }

    ngOnInit(): void {
        this.subscriptions.unshift(this.route.data.subscribe((data: { lineage: IDataLineage }) => {
            this.lineage = data.lineage
            this.lineageStore.lineage = data.lineage
            this.presentHideableOperationTypes =
                _.intersection(
                    _.uniq(data.lineage.operations.map(typeOfOperation)),
                    this.hideableOperationTypes)
        }))

        this.subscriptions.unshift(this.route.paramMap.subscribe(pm => {
            let opId = pm.get("operationId")
            this.selectedOperation = this.lineageStore.lineageAccessors.getOperation(opId)
        }))

        this.subscriptions.unshift(this.route.queryParamMap.subscribe(qps => {
            this.selectedAttrIDs = qps.getAll("attr")
            this.highlightedNodeIDs = this.lineageStore.lineageAccessors.getOperationIdsByAnyAttributeId(...this.selectedAttrIDs)
            this.hiddenOperationTypes = <OperationType[]> qps.getAll("hideOp")
        }))

        this.subscriptions.unshift(this.route.fragment.subscribe(fragment => {
            this.selectedTabIndex = Tab.fromFragment(fragment).valueOr(this.selectedTabIndex)
        }))
    }

    ngOnDestroy(): void {
        this.subscriptions.forEach(s => s.unsubscribe())
    }

    getDataSourceCount() {
        return _.sumBy(this.lineage.operations, node => +(typeOfOperation(node) == 'Read'))
    }

    onOperationSelected(opId: string) {
        if (opId)
            this.router.navigate(["op", opId], {
                    relativeTo: this.route.parent,
                    fragment: Tab.toFragment(Tab.Operation),
                    queryParams: {},
                    queryParamsHandling: "merge"
                }
            )
        else
            this.router.navigate(["."], {
                    relativeTo: this.route.parent,
                    fragment: Tab.toFragment(Tab.Summary),
                    queryParams: {},
                    queryParamsHandling: "merge"
                }
            )
    }

    onTabChanged(e: MatTabChangeEvent) {
        this.router.navigate([], {
            fragment: Tab.toFragment(e.index),
            queryParamsHandling: "preserve"
        })

    }

    onAttributeSelected(attr: IAttribute) {
        this.doSelectAttribute(attr.id)
    }

    clearSelection() {
        this.doSelectAttribute()
    }

    gotoLineageOverview() {
        this.router.navigate(["overview"], {
            fragment: "datasource",
            relativeTo: this.route.parent.parent
        })
    }

    private doSelectAttribute(...attrIds: string[]) {
        this.router.navigate([], {
            queryParams: {'attr': attrIds},
            queryParamsHandling: "merge",
            preserveFragment: true
        })
    }

    toggleOperationTypeVisibility(opType: OperationType) {
        let otherHiddenOpTypes = _.without(this.hiddenOperationTypes, opType),
            updatedHiddenOperationTypes = (this.hiddenOperationTypes.length > otherHiddenOpTypes.length)
                ? otherHiddenOpTypes
                : this.hiddenOperationTypes.concat(opType)

        this.router.navigate([], {
            queryParams: {'hideOp': updatedHiddenOperationTypes},
            queryParamsHandling: "merge",
            preserveFragment: true
        })
    }
}