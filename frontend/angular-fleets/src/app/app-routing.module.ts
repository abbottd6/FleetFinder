import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { WelcomeScreenComponent } from "./components/welcome-screen/welcome-screen.component";
import { GroupListingsComponent} from "./components/group-listings/group-listings.component";
import { CreateListingComponent} from "./components/create-listing/create-listing.component";

const routes: Routes = [
  { path: '', component: WelcomeScreenComponent },
  { path: 'group-listings', component: GroupListingsComponent },
  { path: 'create-listing', component: CreateListingComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
