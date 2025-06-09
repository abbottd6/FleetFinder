import {Component, OnInit} from '@angular/core';
import {UserService} from "../../services/user-services/user.service";
import {PublicUser} from "../../models/public-user/public-user";

@Component({
    selector: 'app-user',
    templateUrl: './user.component.html',
    styleUrl: './user.component.css',
    standalone: false
})
export class UserComponent implements OnInit{
  user: PublicUser | undefined;

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.userService.getUserById(1).subscribe(data => {
      this.user = data;
    })
  }
}
