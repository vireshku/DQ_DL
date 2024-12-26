/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, Resolve} from "@angular/router";

import {IPersistedDatasetDescriptor} from "../../generated-ts/lineage-model";
import {DatasetService} from "./dataset.service";

@Injectable()
export class PersistentDatasetResolver implements Resolve<IPersistedDatasetDescriptor> {

    constructor(private datasetService: DatasetService) {
    }

    resolve(route: ActivatedRouteSnapshot): Promise<IPersistedDatasetDescriptor> {
        let dsId = route.paramMap.get('id')
        return this.datasetService.getDatasetDescriptor(dsId)
    }

}