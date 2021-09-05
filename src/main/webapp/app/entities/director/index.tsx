import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Director from './director';
import DirectorDetail from './director-detail';
import DirectorUpdate from './director-update';
import DirectorDeleteDialog from './director-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={DirectorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={DirectorUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={DirectorDetail} />
      <ErrorBoundaryRoute path={match.url} component={Director} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={DirectorDeleteDialog} />
  </>
);

export default Routes;
