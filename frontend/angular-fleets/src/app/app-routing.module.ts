import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WelcomeScreenComponent } from "./components/welcome-screen/welcome-screen.component";
import { GroupListingsComponent} from "./components/group-listings/group-listings.component";
import { CreateListingComponent} from "./components/create-listing/create-listing.component";
import { UserComponent} from "./components/user/user.component";
import {AboutComponent} from "./components/about/about.component";

const routes: Routes = [
  { path: '', component: WelcomeScreenComponent },
  { path: 'group-listings', component: GroupListingsComponent },
  { path: 'create-listing', component: CreateListingComponent },
  { path: 'user-account', component: UserComponent },
  { path: 'about', component: AboutComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
