import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './director.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const DirectorDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const directorEntity = useAppSelector(state => state.director.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="directorDetailsHeading">Director</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{directorEntity.id}</dd>
          <dt>
            <span id="din">Din</span>
          </dt>
          <dd>{directorEntity.din}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{directorEntity.name}</dd>
          <dt>
            <span id="beginDate">Begin Date</span>
          </dt>
          <dd>
            {directorEntity.beginDate ? <TextFormat value={directorEntity.beginDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endDate">End Date</span>
          </dt>
          <dd>
            {directorEntity.endDate ? <TextFormat value={directorEntity.endDate} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>Company</dt>
          <dd>{directorEntity.company ? directorEntity.company.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/director" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/director/${directorEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default DirectorDetail;
