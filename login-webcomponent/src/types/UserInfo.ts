/**
 *
 * @export
 * @interface UserInfo
 */
export interface UserInfo {
  /**
   *
   * @type {string}
   * @memberof UserInfo
   */
  accountSource: string;
  /**
   *
   * @type {string}
   * @memberof UserInfo
   */
  displayName: string;
  /**
   *
   * @type {string}
   * @memberof UserInfo
   */
  email: string;
  /**
   *
   * @type {string}
   * @memberof UserInfo
   */
  authlevel?: string;
  /**
   *
   * @type {string}
   * @memberof UserInfo
   */
  handelndePersonVorname?: string;
  /**
   *
   * @type {string}
   * @memberof UserInfo
   */
  handelndePersonNachname?: string;
}
