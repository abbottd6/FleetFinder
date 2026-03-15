import {Injectable, Input, OnDestroy} from '@angular/core';
import {filter, firstValueFrom, map, Observable, Subject, take} from "rxjs";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {UserService} from "./user.service";
import {ServerRegion} from "../../models/reference-data/reference-data.models";
import {LookupService} from "../api-services/reference-data-api/lookup.service";

type UpdateUserForm = {
  server: FormControl<ServerRegion | null>;
  org: FormControl<string | null>;
}

@Injectable({
  providedIn: 'root'
})
export class UpdateUserFormService implements OnDestroy {

  private destroy$: Subject<void> = new Subject<void>();

  public orgCharacterCount: number = 0;
  public serverCharacterCount: number = 0;

  userForm!: FormGroup<UpdateUserForm>;
  userServer!: ServerRegion | undefined;

  constructor(private formBuilder: FormBuilder, private userService: UserService, private lookupSrv: LookupService) {
    this.userForm = this.buildUserUpdateForm();

    lookupSrv.getServerRegions().pipe(
      take(1),
      map(servers => servers.find(s => s.servername == this.userService.server)))
        .subscribe(server => {
          this.userServer = server
        });

    this.userForm.patchValue({
      server: this.userServer ?? null,
      org: this.userService.org,
    })
  }

  private buildUserUpdateForm() {
    return this.formBuilder.group<UpdateUserForm>({
      server: this.formBuilder.control<ServerRegion | null>(null),
      org: this.formBuilder.control<string | null>(null, [Validators.maxLength(32)]),
    })
  }

  orgUpdateCharacterCount() {
    const value = this.orgControl?.value || '';
    this.orgCharacterCount = value.length;
  }

  serverUpdateCharacterCount() {}

  get orgControl() {
    return this.userForm.get('org') as FormControl;
  }

  get serverControl() {
    return this.userForm.get('server') as FormControl;
  }

  ngOnDestroy() {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
