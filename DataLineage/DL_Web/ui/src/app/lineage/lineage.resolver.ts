/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, Resolve} from "@angular/router";
import {IDataLineage, IPersistedDatasetDescriptor} from "../../generated-ts/lineage-model";
import {LineageService} from "./lineage.service";

@Injectable()
export class LineageByDatasetIdResolver implements Resolve<IDataLineage> {

    constructor(private lineageService: LineageService) {
    }

    resolve(route: ActivatedRouteSnapshot): Promise<IDataLineage> {
        let dataset: IPersistedDatasetDescriptor = route.parent.data['dataset']
        return this.lineageService.getLineage(dataset.datasetId)
    }
}