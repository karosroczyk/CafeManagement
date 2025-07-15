import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UserService extends BaseService {

  private readonly apiUrl = this.rootUrl + '/api/categories';

  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }
}
