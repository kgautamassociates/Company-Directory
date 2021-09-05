import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Company e2e test', () => {
  const companyPageUrl = '/company';
  const companyPageUrlPattern = new RegExp('/company(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'admin';
  const password = Cypress.env('E2E_PASSWORD') ?? 'admin';

  before(() => {
    cy.window().then(win => {
      win.sessionStorage.clear();
    });
    cy.visit('');
    cy.login(username, password);
    cy.get(entityItemSelector).should('exist');
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/companies+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/companies').as('postEntityRequest');
    cy.intercept('DELETE', '/api/companies/*').as('deleteEntityRequest');
  });

  it('should load Companies', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('company');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Company').should('exist');
    cy.url().should('match', companyPageUrlPattern);
  });

  it('should load details Company page', function () {
    cy.visit(companyPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityDetailsButtonSelector).first().click({ force: true });
    cy.getEntityDetailsHeading('company');
    cy.get(entityDetailsBackButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', companyPageUrlPattern);
  });

  it('should load create Company page', () => {
    cy.visit(companyPageUrl);
    cy.wait('@entitiesRequest');
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Company');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', companyPageUrlPattern);
  });

  it('should load edit Company page', function () {
    cy.visit(companyPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length === 0) {
        this.skip();
      }
    });
    cy.get(entityEditButtonSelector).first().click({ force: true });
    cy.getEntityCreateUpdateHeading('Company');
    cy.get(entityCreateSaveButtonSelector).should('exist');
    cy.get(entityCreateCancelButtonSelector).click({ force: true });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', companyPageUrlPattern);
  });

  it('should create an instance of Company', () => {
    cy.visit(companyPageUrl);
    cy.get(entityCreateButtonSelector).click({ force: true });
    cy.getEntityCreateUpdateHeading('Company');

    cy.get(`[data-cy="cin"]`).type('Polarised B2C Consultant').should('have.value', 'Polarised B2C Consultant');

    cy.get(`[data-cy="name"]`).type('ADP').should('have.value', 'ADP');

    cy.get(`[data-cy="registeredAddress"]`).type('Island cultivate content-based').should('have.value', 'Island cultivate content-based');

    cy.get(`[data-cy="dateOfIncorporation"]`).type('2021-09-04').should('have.value', '2021-09-04');

    cy.get(`[data-cy="authorisedCapital"]`).type('9073').should('have.value', '9073');

    cy.get(`[data-cy="paidUpCapital"]`).type('68993').should('have.value', '68993');

    cy.get(`[data-cy="emailId"]`).type('Square cutting-edge').should('have.value', 'Square cutting-edge');

    cy.get(`[data-cy="dateOfLastAGM"]`).type('2021-09-05').should('have.value', '2021-09-05');

    cy.get(`[data-cy="dateOfBalanceSheet"]`).type('2021-09-05').should('have.value', '2021-09-05');

    cy.get(`[data-cy="companyStatus"]`).type('Intranet').should('have.value', 'Intranet');

    cy.get(`[data-cy="rocCode"]`).type('Decentralized').should('have.value', 'Decentralized');

    cy.get(entityCreateSaveButtonSelector).click({ force: true });
    cy.scrollTo('top', { ensureScrollable: false });
    cy.get(entityCreateSaveButtonSelector).should('not.exist');
    cy.wait('@postEntityRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(201);
    });
    cy.wait('@entitiesRequest').then(({ response }) => {
      expect(response.statusCode).to.equal(200);
    });
    cy.url().should('match', companyPageUrlPattern);
  });

  it('should delete last instance of Company', function () {
    cy.intercept('GET', '/api/companies/*').as('dialogDeleteRequest');
    cy.visit(companyPageUrl);
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response.body.length > 0) {
        cy.get(entityTableSelector).should('have.lengthOf', response.body.length);
        cy.get(entityDeleteButtonSelector).last().click({ force: true });
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('company').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click({ force: true });
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response.statusCode).to.equal(200);
        });
        cy.url().should('match', companyPageUrlPattern);
      } else {
        this.skip();
      }
    });
  });
});
