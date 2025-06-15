import { PublicUser } from './public-user';

describe('User', () => {
  it('should create an instance', () => {
    expect(new PublicUser(44, 'Angular Test PublicUser',
      'test@gmail.com', 'USA', 'None')).toBeTruthy();
  });
});
