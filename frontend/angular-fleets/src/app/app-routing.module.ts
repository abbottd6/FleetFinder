import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WelcomeScreenComponent } from "./components/welcome-screen/welcome-screen.component";
import { GroupListingsComponent} from "./components/group-listings/group-listings.component";
import { CreateListingComponent} from "./components/create-listing/create-listing.component";
import { UserComponent} from "./components/user/user.component";
import { AboutComponent } from "./components/about/about.component";
import {AuthGuard} from "./services/auth/auth_guards/auth.guard";
import {UpdateListingComponent} from "./components/update-listing/update-listing.component";
import {LoginModalComponent} from "./components/login-modal/login-modal.component";
import {ListingSuccessComponent} from "./components/listing-success/listing-success.component";
import {HowToComponent} from "./components/how-to/how-to.component";


const routes: Routes = [
  { path: '', component: WelcomeScreenComponent },
  { path: 'login', component: LoginModalComponent },
  { path: 'group-listings', component: GroupListingsComponent },
  { path: 'create-listing', component: CreateListingComponent, canActivate: [ AuthGuard ] },
  { path: 'user-account', component: UserComponent, canActivate: [ AuthGuard ] },
  { path: 'update-listing', component: UpdateListingComponent, canActivate: [ AuthGuard ] },
  { path: 'about', component: AboutComponent },
  { path: 'listing-success', component: ListingSuccessComponent, canActivate: [ AuthGuard ] },
  { path: 'how-to', component: HowToComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
