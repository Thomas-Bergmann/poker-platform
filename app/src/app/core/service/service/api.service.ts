import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Store, select } from '@ngrx/store';
import {
  EMPTY,
  MonoTypeOperatorFunction,
  Observable,
  OperatorFunction,
  combineLatest,
  forkJoin,
  of,
  throwError,
} from 'rxjs';
import { catchError, concatMap, first, map } from 'rxjs/operators';

import { restEndpoint, serverError, clientError, serverTimeout, ServiceState } from 'src/app/core/service/store';

/**
 * Pipeable operator for elements translation (removing the envelope).
 * @param key the name of the envelope (default 'elements')
 * @returns The items of an elements array without the elements wrapper.
 */
// tslint:disable-next-line: no-any - any to avoid having to type everything before
export function unpackEnvelope<T>(key: string = 'elements'): OperatorFunction<any, T[]> {
  return map(data => (data?.[key]?.length ? data[key] : []));
}

export interface ApiOptions {
  params?: HttpParams;
  headers?: HttpHeaders;
  responseType?: string;
  useStoreErrorHandling?: boolean;
}

const DEFAULT_HEADER = new HttpHeaders()
  .set('content-type', 'application/json')
  .set('Accept', 'application/json');

@Injectable({ providedIn: 'root' })
export class ApiService {
  static AUTHORIZATION_HEADER_KEY = 'Authorization';

  constructor(
    private httpClient: HttpClient,
    private store: Store<ServiceState>
  ) {}

  /**
   * merges supplied and default headers
   */
  private constructHeaders(options?: ApiOptions): Observable<HttpHeaders> {
    return of(this.addHeaders(DEFAULT_HEADER, options?.headers));
  }

  private addHeaders(headers : HttpHeaders, additionalHeader?: HttpHeaders) : HttpHeaders {
    var result : HttpHeaders = new HttpHeaders();
    headers.keys().forEach(k => {
      var kHeaders = headers.getAll(k);
      if (kHeaders != null) result.set(k, kHeaders)
    });
    if (additionalHeader != null)
    {
      additionalHeader.keys().forEach(k => {
        var kHeaders = headers.getAll(k);
        if (kHeaders != null) result.set(k, kHeaders)
      });
    }
    return result;
  }

  private handleErrors<T>(dispatch?: boolean): MonoTypeOperatorFunction<T> {
    return catchError(error => {
      if (dispatch) {
        if (error.status === 0) {
          this.store.dispatch(serverTimeout());
          return EMPTY;
        } else if (error.status >= 400 && error.status < 500) {
            this.store.dispatch(clientError({ status : error.status, message : error.payload }));
            return EMPTY;
          } else if (error.status >= 500 && error.status < 600) {
          this.store.dispatch(serverError({ status : error.status, message : error.payload }));
          return EMPTY;
        }
      }
      return throwError(error);
    });
  }

  private execute<T>(httpCall$: Observable<T>, options? : ApiOptions): Observable<T> {
    return httpCall$.pipe(this.handleErrors(options == null ? false : options.useStoreErrorHandling));
  }
  private constructUrlForPath(path: string): Observable<string> {
    return combineLatest([
        // base url
        this.store.pipe(select(restEndpoint)),
        // first path segment
        of(path),
      ]).pipe(
        first(),
        map(arr => arr.join(''))
      );
  }
  private constructHttpClientParams(path: string, options?: ApiOptions)
    : Observable<[string, { headers: HttpHeaders; params?: HttpParams }]> {
    return forkJoin([
      this.constructUrlForPath(path),
      this.constructHttpParams(options),
    ]);
  }

  private constructHttpParams(options?: ApiOptions) : Observable<{ headers: HttpHeaders; params?: HttpParams }>
  {
    return this.constructHeaders(options).pipe(
      map(myHeaders => ({
        headers : myHeaders,
        params: options?.params
      })));
  }
  /**
   * http get request
   */
  get<T>(path: string, options?: ApiOptions): Observable<T> {
    return this.execute(
        this.constructHttpClientParams(path, options).pipe(
          concatMap(([url, httpOptions]) => this.httpClient.get<T>(url, httpOptions))
        )
      );
    }

  /**
   * http options request
   */
  options(path: string, options?: ApiOptions): Observable<{}> {
    return this.execute(
      this.constructHttpClientParams(path, options).pipe(
        concatMap(([url, httpOptions]) => this.httpClient.options<{}>(url, httpOptions))
      ), options
    );
  }

  /**
   * http put request
   */
  put(path: string, body = {}, options?: ApiOptions): Observable<{}> {
    return this.execute(
      this.constructHttpClientParams(path, options).pipe(
        concatMap(([url, httpOptions]) => this.httpClient.put<{}>(url, body, httpOptions))
      ), options
    );
  }

  /**
   * http patch request
   */
  patch(path: string, body = {}, options?: ApiOptions): Observable<{}> {
    return this.execute(
      this.constructHttpClientParams(path, options).pipe(
        concatMap(([url, httpOptions]) => this.httpClient.patch<{}>(url, body, httpOptions))
      ), options
    );
  }

  /**
   * http post request
   */
  post<T>(path: string, body = {}, options?: ApiOptions): Observable<T> {
    return this.execute(
      this.constructHttpClientParams(path, options).pipe(
        concatMap(([url, httpOptions]) => this.httpClient.post<T>(url, body, httpOptions))
      ), options
    );
  }

  /**
   * http delete request
   */
  delete<T>(path: string, options?: ApiOptions): Observable<T> {
    return this.execute(
      this.constructHttpClientParams(path, options).pipe(
        concatMap(([url, httpOptions]) => this.httpClient.delete<T>(url, httpOptions))
      ), options
    );
  }
}
