import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './company.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CompanyDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const companyEntity = useAppSelector(state => state.company.entity);

  return (
    <Row>
      <Col md="8">
        <h2 data-cy="companyDetailsHeading">Company</h2>
        <dl className="jh-entity-details">
          <Row>
            <Col md="4">
              <dt>
                <span id="id">ID</span>
              </dt>
              <dd>{companyEntity.id}</dd>
              <dt>
                <span id="cin">Cin</span>
              </dt>
              <dd>{companyEntity.cin}</dd>
              <dt>
                <span id="name">Name</span>
              </dt>
              <dd>{companyEntity.name}</dd>
              <dt>
                <span id="registeredAddress">Registered Address</span>
              </dt>
              <dd>{companyEntity.registeredAddress}</dd>
              <dt>
                <span id="dateOfIncorporation">Date Of Incorporation</span>
              </dt>
              <dd>
                {companyEntity.dateOfIncorporation ? (
                  <TextFormat value={companyEntity.dateOfIncorporation} type="date" format={APP_LOCAL_DATE_FORMAT} />
                ) : null}
              </dd>
              <dt>
                <span id="authorisedCapital">Authorised Capital</span>
              </dt>
              <dd>{companyEntity.authorisedCapital}</dd>
              <dt>
                <span id="paidUpCapital">Paid Up Capital</span>
              </dt>
              <dd>{companyEntity.paidUpCapital}</dd>
            </Col>
            <Col md="4">
              <dt>
                <span id="emailId">Email Id</span>
              </dt>
              <dd>{companyEntity.emailId}</dd>
              <dt>
                <span id="dateOfLastAGM">Date Of Last AGM</span>
              </dt>
              <dd>
                {companyEntity.dateOfLastAGM ? (
                  <TextFormat value={companyEntity.dateOfLastAGM} type="date" format={APP_LOCAL_DATE_FORMAT} />
                ) : null}
              </dd>
              <dt>
                <span id="dateOfBalanceSheet">Date Of Balance Sheet</span>
              </dt>
              <dd>
                {companyEntity.dateOfBalanceSheet ? (
                  <TextFormat value={companyEntity.dateOfBalanceSheet} type="date" format={APP_LOCAL_DATE_FORMAT} />
                ) : null}
              </dd>
              <dt>
                <span id="companyStatus">Company Status</span>
              </dt>
              <dd>{companyEntity.companyStatus}</dd>
              <dt>
                <span id="rocCode">Roc Code</span>
              </dt>
              <dd>{companyEntity.rocCode}</dd>
              <dt>
                <span id="directors">Directors</span>
              </dt>
              <dd>
                {companyEntity.directors ? companyEntity.directors.map(director => <div key={director.id}>{director.name}</div>) : null}
              </dd>
            </Col>
          </Row>
        </dl>
        <Button tag={Link} to="/company" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/company/${companyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CompanyDetail;
