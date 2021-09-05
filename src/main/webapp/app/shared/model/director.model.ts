import dayjs from 'dayjs';
import { ICompany } from 'app/shared/model/company.model';

export interface IDirector {
  id?: number;
  din?: string | null;
  name?: string | null;
  beginDate?: string | null;
  endDate?: string | null;
  company?: ICompany | null;
}

export const defaultValue: Readonly<IDirector> = {};
