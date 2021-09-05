import React, { useState, useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ICompany } from 'app/shared/model/company.model';
import { getEntities as getCompanies } from 'app/entities/company/company.reducer';
import { getEntity, updateEntity, createEntity, reset } from './director.reducer';
import { IDirector } from 'app/shared/model/director.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const DirectorUpdate = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const companies = useAppSelector(state => state.company.entities);
  const directorEntity = useAppSelector(state => state.director.entity);
  const loading = useAppSelector(state => state.director.loading);
  const updating = useAppSelector(state => state.director.updating);
  const updateSuccess = useAppSelector(state => state.director.updateSuccess);

  const handleClose = () => {
    props.history.push('/director' + props.location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(props.match.params.id));
    }

    dispatch(getCompanies({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...directorEntity,
      ...values,
      company: companies.find(it => it.id.toString() === values.companyId.toString()),
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
          ...directorEntity,
          companyId: directorEntity?.company?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="kgaApp.director.home.createOrEditLabel" data-cy="DirectorCreateUpdateHeading">
            Create or edit a Director
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="director-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Din" id="director-din" name="din" data-cy="din" type="text" />
              <ValidatedField label="Name" id="director-name" name="name" data-cy="name" type="text" />
              <ValidatedField label="Begin Date" id="director-beginDate" name="beginDate" data-cy="beginDate" type="date" />
              <ValidatedField label="End Date" id="director-endDate" name="endDate" data-cy="endDate" type="date" />
              <ValidatedField id="director-company" name="companyId" data-cy="company" label="Company" type="select">
                <option value="" key="0" />
                {companies
                  ? companies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/director" replace color="info">
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

export default DirectorUpdate;
