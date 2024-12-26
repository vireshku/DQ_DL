/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {IOperation} from "../../generated-ts/lineage-model";
import {IExpression} from "../../generated-ts/expression-model";
import {IDataType} from "../../generated-ts/datatype-model";

export type OperationType =
    ("Projection"
        | "Read"
        | "Join"
        | "Union"
        | "Generic"
        | "Filter"
        | "Sort"
        | "Aggregate"
        | "Write"
        | "Alias"
        | "Composite"
        )

export type ExpressionType =
    ("Binary"
        | "Literal"
        | "Alias"
        | "UDF"
        | "Generic"
        | "GenericLeaf"
        | "AttrRef"
        )

export type DataTypeType =
    ("Struct"
        | "Array"
        | "Simple"
        )


export function typeOfOperation(node: IOperation): OperationType | undefined {
    return typeOfAnyAssumingPrefix(node, "op") as OperationType
}

export function typeOfExpr(expr: IExpression): ExpressionType | undefined {
    return typeOfAnyAssumingPrefix(expr, "expr") as ExpressionType
}

export function typeOfDataType(dataType: IDataType): DataTypeType | undefined {
    return typeOfAnyAssumingPrefix(dataType, "dt") as DataTypeType
}

function typeOfAnyAssumingPrefix(obj: any, expectedPrefix: string): string | undefined {
    let typeSuffix = getNthLastIndexOf(obj._typeHint, ".", 2)
    let prefixWithType = typeSuffix && typeSuffix.split(".")
    if (prefixWithType && prefixWithType.length == 2) {
        const [prefix, type] = prefixWithType
        return (prefix == expectedPrefix && type) || undefined
    }
    else
        return undefined
}

function getNthLastIndexOf(str: string, search: string, n: number): string | undefined {
    if(!str)
        return undefined
    else {
        let pos = str.length
        for (let i = 0; i < n; i++) {
            pos = str.lastIndexOf(search, pos - 1)
        }
        return str.substring(pos + 1) || undefined
    }
}