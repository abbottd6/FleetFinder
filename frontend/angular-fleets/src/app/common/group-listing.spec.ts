import { GroupListing } from './group-listing';

describe('GroupListing', () => {
  it('should create an instance', () => {
    expect(new GroupListing(33, 44, 1, 'USA', 3,
      'Tech Preview', 1, 'Persistent Universe',
      'Test listing title', 'Angular Test User', 3, 'Learning', 2,
      'Unlawful', 2,'Future/Scheduled', new Date('2025-01-15'),
      8, 'Exploration', 31, 'Prospecting', 3, 'PvX',
      1, 'Stanton', 1, 'Hurston: Stanton I',
      'Theres a lot to explore in Stanton', 7, 3,
      'Pilot, Turret Gunner, Engineer, Medic', 'REQUIRED', 'Discord',
      new Date('2025-01-09'), new Date('2025-01-07'))).toBeTruthy();
  });
});
