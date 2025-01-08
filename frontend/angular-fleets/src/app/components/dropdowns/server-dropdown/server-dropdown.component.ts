import {Component, Input, OnInit, ChangeDetectorRef} from '@angular/core';


@Component({
    selector: 'app-server-dropdown',
    templateUrl: './server-dropdown.component.html',
    styleUrl: './server-dropdown.component.css',
    standalone: false
})
export class ServerDropdownComponent {
  cities = [
    {id: 1, name: 'New York'},
    {id: 2, name: 'Seattle'},
    {id: 3, name: 'Los Angeles'},
  ]
}
  //@Input() control!: FormControl;
  //public servers!: Observable<any[]>;


/*
  constructor(private lookupService: LookupService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.fetchServerRegions();
    //console.log('FormControl Value:', this.control.value);
    //console.log('FormControl Valid:', this.control.valid);
    console.log('Dropdown options on render:', this.servers);
  }


  fetchServerRegions(): void {
    this.servers = this.lookupService.getServerRegions()
        console.log('Dropdown Options:', this.servers);

        //Update FormControl value if servers are available

        if (this.servers.length > 0) {
          this.control.setValue(this.servers[0].serverId);
        }
        console.log('FormControl value after options load:', this.control.value);
        console.log('FormControl valid after options load:', this.control.valid);
        console.log('FormControl errors after load:', this.control.errors);
        console.log('Control status:', this.control.status);


      error: (error) => {
        console.error('Error fetching server regions:', error);
      },
      complete: () => {
        console.log('Fetching complete.');
      }
    });


  }

}
*/
