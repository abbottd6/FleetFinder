import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatIcon} from "@angular/material/icon";
import {MatTab, MatTabContent, MatTabGroup} from "@angular/material/tabs";
import {Subject, takeUntil} from "rxjs";
import {
  GroupManagementMemberViewModel
} from "../../models/group-management-models/view-models/group-membership/group-management-member-view-model";
import {
  GroupManagementInviteViewModel
} from "../../models/group-management-models/view-models/group-membership/group-management-invite-view-model";
import {UserService} from "../../services/user-services/user.service";
import {ActivatedRoute, Router} from "@angular/router";
import {MemberManagementApiService} from "../../services/api-services/group-management/member-management-api.service";
import {HttpErrorResponse} from "@angular/common/http";
import {NgForOf, NgIf} from "@angular/common";
import {MatProgressSpinner} from "@angular/material/progress-spinner";

@Component({
  selector: 'app-group-management-page',
  standalone: true,
  templateUrl: './group-management-page.component.html',
  imports: [
    MatIcon,
    MatTabGroup,
    MatTab,
    MatTabContent,
    NgIf,
    MatProgressSpinner,
    NgForOf
  ],
  styleUrl: './group-management-page.component.css'
})
export class GroupManagementPageComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>;

  protected activeRoster: GroupManagementMemberViewModel[] = [];
  protected waitlistRoster: GroupManagementMemberViewModel[] = [];
  protected noWaitlistMembers: boolean = false;
  protected groupInvites: GroupManagementInviteViewModel[] = [];
  protected noGroupInvites: boolean = false;

  protected isLoading: boolean = true;

  protected groupId!: number | null;

  constructor(private userService: UserService,
              private router: Router,
              protected memberManagementApi: MemberManagementApiService,
              private route: ActivatedRoute) {}

  ngOnInit() {
    this.isLoading = true;
    if(!this.userService.userLoggedIn) {
      this.router.navigateByUrl('');
    }

    const stringId = this.route.snapshot.paramMap.get('groupId');

    this.groupId = Number(stringId) ? Number(stringId) : null;

    if(!this.groupId) {
      this.router.navigateByUrl('/nothing-here-page')
      return;
    }

    this.memberManagementApi.verifyGroupManagementAuthorization(this.groupId).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: response => {
          if(response.get('isAuthorized') === true) {
            console.log('This stuff is great.')
          }
        },
        error: (e: HttpErrorResponse) => {
          if(e.status === 401) {
            alert('You are not authorized to manage this group.');
            this.router.navigateByUrl('/user-account')
          } else {
            alert('There was an error retrieving this listing. ' +
              'Please try again later or submit a ticket for help.')
            this.router.navigateByUrl('/user-account')
          }
        }
    });

    //todo move these into their respective components and lazy load them.

    this.memberManagementApi.getActiveRosterGroupMembers(this.groupId).pipe(takeUntil(this.destroy$))
      .subscribe(page => {
        this.activeRoster = page.content;
      });

    this.memberManagementApi.getGroupInvites(this.groupId).pipe(takeUntil(this.destroy$))
      .subscribe(page => {
        this.groupInvites = page.content;
        this.noGroupInvites = page.content.length === 0;
      });

    this.isLoading = false;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

}
