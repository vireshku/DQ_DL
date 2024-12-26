/*
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */

export class SearchRequest {
    constructor(public readonly text: string,
                public readonly asAtTime = Date.now(),
                public readonly offset: number = 0) {
    }

    public withOffset(offset: number): SearchRequest {
        return offset == this.offset
            ? this
            : new SearchRequest(this.text,
                this.asAtTime,
                offset)
    }
}