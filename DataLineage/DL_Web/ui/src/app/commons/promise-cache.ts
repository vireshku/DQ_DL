/**
 *
 * @author      viresh kumar <virkumar@microsoft.com.com>
 * @designer		viresh kumar <virkumar@microsoft.com.com>
 * @developer   viresh kumar <virkumar@microsoft.com.com>
 * @version     1.0
 * @since       1.0
 */


export class PromiseCache<V> {
    private cache: { [key: string]: Promise<V>; } = {}

    public put(key: string, value: Promise<V>): this {
        this.cache[key] = value
        return this
    }

    public get(key: string): Promise<V> | undefined {
        return this.cache[key]
    }

    public getOrCreate(key: string, create: (key: string) => Promise<V>): Promise<V> {
        let value: Promise<V> = this.cache[key]
        if (!value) {
            value = create(key).catch((err) => {
                delete this.cache[key]
                throw err
            }) as Promise<V>
            this.cache[key] = value
        }
        return value
    }
}