import type {ReactNode} from 'react';
import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  Svg: React.ComponentType<React.ComponentProps<'svg'>>;
  description: ReactNode;
};

const FeatureList: FeatureItem[] = [
  {
    title: '✅ Time-Triggered UI That Just Works',
    Svg: require('@site/static/img/undraw_docusaurus_mountain.svg').default,
    description: (
      <>
        Reliable alarms that launch your UI even if the app’s killed or in Doze. No hacks, just results.
      </>
    ),
  },
  {
    title: '⚙️ Fully Custom UI with Compose',
    Svg: require('@site/static/img/undraw_docusaurus_tree.svg').default,
    description: (
      <>
        Build any screen you want using Jetpack Compose. We’ll take care of showing it.
      </>
    ),
  },
  {
    title: '🔐 Handles Permissions So You Don’t Have To',
    Svg: require('@site/static/img/undraw_docusaurus_react.svg').default,
    description: (
      <>
        One-liner permission flow. No boilerplate, no confusion.
      </>
    ),
  },
];

function Feature({title, Svg, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
        <Svg className={styles.featureSvg} role="img" />
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): ReactNode {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
