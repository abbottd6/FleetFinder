import { TestBed } from '@angular/core/testing';

import { WsGatewayService } from './ws-gateway.service';

describe('WsGatewayService', () => {
  let service: WsGatewayService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WsGatewayService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
