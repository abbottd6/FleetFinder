import { CreateListingRequest } from './create-listing-request';

describe('CreateListingModel', () => {
  it('should create an instance', () => {
    const formData = {
      titleGroup: { listingTitle: 'Test' },
      sessionEnvInfoGroup: { serverRegion: 1, gameEnvironment: 1, gameExperience: 1 },
      gameplayInfoGroup: { playStyle: null, category: 1, subcategory: null, legality: 1, pvpStatus: 1, planetarySystem: 1, planetMoon: null, listingDescription: 'test' },
      groupSpecInfoGroup: { groupStatus: 1, eventScheduleDate: null, eventScheduleTime: null, eventScheduleZone: null, currentPartySize: 2, desiredPartySize: 4, availableRoles: null, commsOption: 'Optional', commsService: null, languageCode: 'EN' }
    };
    expect(new CreateListingRequest(formData)).toBeTruthy();
  });
});
