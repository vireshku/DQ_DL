/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import "./third-party-scripts/browser-check.exec.js";
import "es6-shim";
import "reflect-metadata";
import "zone.js";
import {platformBrowserDynamic} from "@angular/platform-browser-dynamic";
import {AppModule} from "./app/app.module";
import {enableProdMode} from "@angular/core";

declare const __PRODUCTION_MODE__: boolean
if (__PRODUCTION_MODE__) enableProdMode()

platformBrowserDynamic().bootstrapModule(AppModule)