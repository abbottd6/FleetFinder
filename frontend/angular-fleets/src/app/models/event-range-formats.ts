import {MatDateFormats} from "@angular/material/core";

export const EVENT_RANGE_FORMATS: MatDateFormats = {
  parse: {
    dateInput: 'MM/dd/yyyy',
  },
  display: {
    dateInput: { month: '2-digit', day: '2-digit' },
    monthYearLabel: { month: 'short', year: 'numeric' },
    dateA11yLabel: { year: 'numeric', month: 'long', day: 'numeric' },
    monthYearA11yLabel: { year: 'numeric', month: 'long' },
  }
};
