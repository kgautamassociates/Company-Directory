import dayjs from 'dayjs';
import { IDirector } from 'app/shared/model/director.model';

export interface ICompany {
  id?: number;
  cin?: string;
  name?: string | null;
  registeredAddress?: string | null;
  dateOfIncorporation?: string | null;
  authorisedCapital?: number | null;
  paidUpCapital?: number | null;
  emailId?: string | null;
  dateOfLastAGM?: string | null;
  dateOfBalanceSheet?: string | null;
  companyStatus?: string | null;
  rocCode?: string | null;
  directors?: IDirector[] | null;
}

export const defaultValue: Readonly<ICompany> = {};
