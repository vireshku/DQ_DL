/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

import {Injectable} from "@angular/core";
import {IPersistedDatasetDescriptor} from "../../../generated-ts/lineage-model";
import {HttpClient} from "@angular/common/http";
import {SearchRequest} from "./dataset-browser.model";

@Injectable()
export class DatasetBrowserService {
    constructor(private httpClient: HttpClient) {
    }

    getLineageDescriptors(searchRequest: SearchRequest): Promise<IPersistedDatasetDescriptor[]> {
        return this.httpClient.get<IPersistedDatasetDescriptor[]>(
            "rest/dataset/descriptors",
            {
                params: {
                    q: searchRequest.text,
                    asAtTime: `${searchRequest.asAtTime}`,
                    offset: `${searchRequest.offset}`,
                    size: "20"
                }
            }
        ).toPromise()
    }
}