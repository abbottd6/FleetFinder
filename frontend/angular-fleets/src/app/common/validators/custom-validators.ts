import {AbstractControl, FormControl, FormGroup, ValidationErrors, ValidatorFn} from "@angular/forms";

//Custom validation for eventScheduleDate, eventScheduleTime, and eventScheduleZone to make these fields required if
//group status is 'future/scheduled'
export const requiredIfGroupStatusFuture: ValidatorFn = (
  control: AbstractControl,): ValidationErrors | null => {
    const parent = control.parent;

    console.log('Validator triggered:', {control, parent});

    if(!parent) {
      return null;
    }

    const groupStatus = parent.get('groupStatus');
    console.log('Group status control: ', groupStatus);

    if (!groupStatus) {
      return null;
    }

    const groupStatusValue = groupStatus.value;

    if (groupStatusValue === 2 && !control.value) {
      return { required: true };
    }

    return null;
}


