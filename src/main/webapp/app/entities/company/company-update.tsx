import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity, updateEntity, createEntity, reset } from './company.reducer';
import { ICompany } from 'app/shared/model/company.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CompanyUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const companyEntity = useAppSelector(state => state.company.entity);
  const loading = useAppSelector(state => state.company.loading);
  const updating = useAppSelector(state => state.company.updating);
  const updateSuccess = useAppSelector(state => state.company.updateSuccess);

  const handleClose = () => {
    props.history.push('/company' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...companyEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...companyEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kgaApp.company.home.createOrEditLabel" data-cy="CompanyCreateUpdateHeading">
            Create or edit a Company
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="company-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Cin"
                id="company-cin"
                name="cin"
                data-cy="cin"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField label="Name" id="company-name" name="name" data-cy="name" type="text" />
              <ValidatedField
                label="Registered Address"
                id="company-registeredAddress"
                name="registeredAddress"
                data-cy="registeredAddress"
                type="text"
              />
              <ValidatedField
                label="Date Of Incorporation"
                id="company-dateOfIncorporation"
                name="dateOfIncorporation"
                data-cy="dateOfIncorporation"
                type="date"
              />
              <ValidatedField
                label="Authorised Capital"
                id="company-authorisedCapital"
                name="authorisedCapital"
                data-cy="authorisedCapital"
                type="text"
              />
              <ValidatedField label="Paid Up Capital" id="company-paidUpCapital" name="paidUpCapital" data-cy="paidUpCapital" type="text" />
              <ValidatedField label="Email Id" id="company-emailId" name="emailId" data-cy="emailId" type="text" />
              <ValidatedField
                label="Date Of Last AGM"
                id="company-dateOfLastAGM"
                name="dateOfLastAGM"
                data-cy="dateOfLastAGM"
                type="date"
              />
              <ValidatedField
                label="Date Of Balance Sheet"
                id="company-dateOfBalanceSheet"
                name="dateOfBalanceSheet"
                data-cy="dateOfBalanceSheet"
                type="date"
              />
              <ValidatedField label="Company Status" id="company-companyStatus" name="companyStatus" data-cy="companyStatus" type="text" />
              <ValidatedField label="Roc Code" id="company-rocCode" name="rocCode" data-cy="rocCode" type="text" />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/company" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CompanyUpdate;
