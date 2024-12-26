/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Component, Input, OnChanges} from "@angular/core";
import {MatDialog} from "@angular/material";
import {ExpressionDialogComponent} from "./expression-dialog.component";
import {IExpression} from "../../../../generated-ts/expression-model";
import {ExpressionRenderService} from "./expression-render.service";

@Component({
    selector: "expression-inline",
    template: `
        <a href="" (click)="openExprViewDialog($event)">
            <code title="{{ exprString }}">{{ exprString }}</code>
        </a>
    `,
    styles: [`
        code {
            padding: 0;
        }
    `],
    providers: [ExpressionRenderService]
})
export class ExpressionInlineComponent implements OnChanges {
    @Input() expr: IExpression

    exprString: string

    constructor(private dialog: MatDialog,
                private expressionRenderer: ExpressionRenderService) {
    }

    ngOnChanges(): void {
        this.exprString = this.expressionRenderer.getText(this.expr)
    }

    openExprViewDialog(e: Event) {
        e.preventDefault()
        this.dialog.open(ExpressionDialogComponent, <any>{
            data: {
                expr: this.expr,
                exprString: this.exprString,
                expressionRenderer: this.expressionRenderer
            }
        })
    }
}