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
import {IDataLineage, IPersistedDatasetDescriptor} from "../../../generated-ts/lineage-model";
import {DatasetService} from "../dataset.service";

@Injectable()
export class DatasetLineageOverviewResolver implements Resolve<IDataLineage> {

    constructor(private datasetService: DatasetService) {
    }

    resolve(route: ActivatedRouteSnapshot): Promise<IDataLineage> {
        let dataset: IPersistedDatasetDescriptor = route.parent.data['dataset']
        return this.datasetService.getLineageOverview(dataset.datasetId)
    }

}